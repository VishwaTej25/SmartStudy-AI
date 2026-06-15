#!/usr/bin/env python3
"""
SmartStudy Mock Backend API - for DAST testing
Simulates the SmartStudy REST API with proper endpoints and security controls.
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import jwt
import json
import time
from datetime import datetime, timedelta
from functools import wraps

app = Flask(__name__)
CORS(app)

# Configuration
JWT_SECRET = "test_secret_key_dev_only"
JWT_ALGORITHM = "HS256"

# In-memory storage (for demo)
users_db = {
    "user123": {
        "uid": "user123",
        "email": "user@smartstudy.com",
        "fullName": "Test User",
        "role": "user",
        "xp": 100,
        "streak": 5,
    },
    "admin456": {
        "uid": "admin456",
        "email": "admin@smartstudy.com",
        "fullName": "Admin User",
        "role": "admin",
        "xp": 500,
        "streak": 20,
    }
}

courses_db = {
    "1": {"id": "1", "title": "Python Basics", "order": 1, "published": True},
    "2": {"id": "2", "title": "Web Development", "order": 2, "published": True},
    "3": {"id": "3", "title": "Advanced Python", "order": 3, "published": False},
}

# Generate test tokens
def generate_token(uid, role, exp_hours=24):
    payload = {
        "uid": uid,
        "role": role,
        "iat": datetime.utcnow(),
        "exp": datetime.utcnow() + timedelta(hours=exp_hours),
    }
    return jwt.encode(payload, JWT_SECRET, algorithm=JWT_ALGORITHM)

# Test tokens
TOKEN_USER = generate_token("user123", "user")
TOKEN_ADMIN = generate_token("admin456", "admin")
TOKEN_EXPIRED = jwt.encode(
    {"uid": "user123", "role": "user", "exp": datetime.utcnow() - timedelta(hours=1)},
    JWT_SECRET,
    algorithm=JWT_ALGORITHM
)

# Middleware: JWT verification
def require_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get("Authorization", "").replace("Bearer ", "")
        
        if not token:
            return jsonify({"error": "Missing authentication token"}), 401
        
        try:
            payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
            request.user = payload
            return f(*args, **kwargs)
        except jwt.ExpiredSignatureError:
            return jsonify({"error": "Token expired"}), 401
        except jwt.InvalidTokenError:
            return jsonify({"error": "Invalid token"}), 401
    
    return decorated

def require_role(required_role):
    def decorator(f):
        @wraps(f)
        def decorated(*args, **kwargs):
            token = request.headers.get("Authorization", "").replace("Bearer ", "")
            
            if not token:
                return jsonify({"error": "Missing authentication token"}), 401
            
            try:
                payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
                if payload.get("role") != required_role and payload.get("role") != "admin":
                    return jsonify({"error": "Insufficient permissions"}), 403
                request.user = payload
                return f(*args, **kwargs)
            except jwt.ExpiredSignatureError:
                return jsonify({"error": "Token expired"}), 401
            except jwt.InvalidTokenError:
                return jsonify({"error": "Invalid token"}), 401
        
        return decorated
    return decorator

# Routes

@app.route("/api/v1/health", methods=["GET"])
def health():
    """Public health check."""
    return jsonify({"status": "healthy", "timestamp": datetime.utcnow().isoformat()}), 200

@app.route("/api/v1/auth/login", methods=["POST"])
def login():
    """Login endpoint - public."""
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")
    
    if not email or not password:
        return jsonify({"error": "Email and password required"}), 400
    
    # Demo: check credentials
    for uid, user in users_db.items():
        if user["email"] == email:
            token = generate_token(uid, user["role"])
            return jsonify({
                "token": token,
                "user": {"uid": uid, "email": user["email"], "role": user["role"]}
            }), 200
    
    return jsonify({"error": "Invalid credentials"}), 401

@app.route("/api/v1/auth/logout", methods=["POST"])
@require_auth
def logout():
    """Logout endpoint - requires auth."""
    return jsonify({"message": "Logged out successfully"}), 200

@app.route("/api/v1/users", methods=["GET"])
@require_auth
def get_users():
    """Get all users - requires auth."""
    return jsonify({"users": list(users_db.values())}), 200

@app.route("/api/v1/users/<uid>", methods=["GET"])
@require_auth
def get_user(uid):
    """Get specific user - requires auth."""
    if uid not in users_db:
        return jsonify({"error": "User not found"}), 404
    
    return jsonify(users_db[uid]), 200

@app.route("/api/v1/profile", methods=["GET"])
@require_auth
def get_profile():
    """Get current user profile - requires auth."""
    user_id = request.user.get("uid")
    if user_id not in users_db:
        return jsonify({"error": "User not found"}), 404
    
    return jsonify(users_db[user_id]), 200

@app.route("/api/v1/profile", methods=["PUT"])
@require_auth
def update_profile():
    """Update current user profile - requires auth."""
    user_id = request.user.get("uid")
    data = request.get_json()
    
    if user_id not in users_db:
        return jsonify({"error": "User not found"}), 404
    
    users_db[user_id].update(data)
    return jsonify({"message": "Profile updated", "user": users_db[user_id]}), 200

@app.route("/api/v1/settings", methods=["GET"])
@require_auth
def get_settings():
    """Get user settings - requires auth."""
    return jsonify({"settings": {"theme": "dark", "notifications": True, "language": "en"}}), 200

@app.route("/api/v1/settings", methods=["PUT"])
@require_auth
def update_settings():
    """Update user settings - requires auth."""
    data = request.get_json()
    return jsonify({"message": "Settings updated", "settings": data}), 200

@app.route("/api/v1/courses", methods=["GET"])
def get_courses():
    """Get all courses - public endpoint."""
    return jsonify({"courses": list(courses_db.values())}), 200

@app.route("/api/v1/courses/<course_id>", methods=["GET"])
def get_course(course_id):
    """Get specific course - public."""
    if course_id not in courses_db:
        return jsonify({"error": "Course not found"}), 404
    
    return jsonify(courses_db[course_id]), 200

@app.route("/api/v1/courses", methods=["POST"])
@require_role("admin")
def create_course():
    """Create course - admin only."""
    data = request.get_json()
    course_id = str(len(courses_db) + 1)
    course = {"id": course_id, **data}
    courses_db[course_id] = course
    return jsonify({"message": "Course created", "course": course}), 201

@app.route("/api/v1/admin/users", methods=["GET"])
@require_role("admin")
def admin_get_users():
    """Admin view all users - admin only."""
    return jsonify({"users": list(users_db.values())}), 200

@app.route("/api/v1/enrollments", methods=["GET"])
@require_auth
def get_enrollments():
    """Get user enrollments - requires auth."""
    return jsonify({"enrollments": [{"courseId": "1", "progress": 45}, {"courseId": "2", "progress": 20}]}), 200

@app.route("/api/v1/enrollments", methods=["POST"])
@require_auth
def create_enrollment():
    """Enroll in course - requires auth."""
    data = request.get_json()
    return jsonify({"message": "Enrolled successfully", "enrollment": data}), 201

@app.route("/api/v1/payments/history", methods=["GET"])
@require_auth
def get_payment_history():
    """Get payment history - requires auth."""
    return jsonify({"payments": []}), 200

# Error handlers
@app.errorhandler(404)
def not_found(error):
    return jsonify({"error": "Endpoint not found"}), 404

@app.errorhandler(405)
def method_not_allowed(error):
    return jsonify({"error": "Method not allowed"}), 405

@app.errorhandler(500)
def internal_error(error):
    return jsonify({"error": "Internal server error"}), 500

if __name__ == "__main__":
    print("=" * 60)
    print("SmartStudy Mock Backend Starting...")
    print("=" * 60)
    print(f"\nBase URL: http://localhost:5000")
    print(f"\nTest Tokens:")
    print(f"  USER:  {TOKEN_USER}")
    print(f"  ADMIN: {TOKEN_ADMIN}")
    print(f"\nPublic Endpoints:")
    print(f"  GET  /api/v1/health")
    print(f"  POST /api/v1/auth/login")
    print(f"  GET  /api/v1/courses")
    print(f"\nProtected Endpoints (require token):")
    print(f"  POST /api/v1/auth/logout")
    print(f"  GET  /api/v1/users")
    print(f"  GET  /api/v1/profile")
    print(f"  GET  /api/v1/settings")
    print(f"\nAdmin Endpoints (require admin token):")
    print(f"  GET  /api/v1/admin/users")
    print(f"  POST /api/v1/courses")
    print("\n" + "=" * 60 + "\n")
    
    app.run(host="0.0.0.0", port=5000, debug=False)
