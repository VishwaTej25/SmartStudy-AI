# SmartStudy API - Discovered Endpoints

## Summary
- **Base URL**: http://localhost:5000
- **API Version**: v1
- **Total Endpoints**: 14
- **Public Endpoints**: 4
- **Protected Endpoints**: 8
- **Admin-Only Endpoints**: 2

## Endpoint List

### Public Endpoints (No Authentication Required)
1. **GET /api/v1/health** - Health check
2. **POST /api/v1/auth/login** - User login
3. **GET /api/v1/courses** - Get all courses
4. **GET /api/v1/courses/{courseId}** - Get specific course

### Protected Endpoints (Requires Valid User Token)
5. **POST /api/v1/auth/logout** - Logout (requires auth)
6. **GET /api/v1/users** - Get all users (requires auth)
7. **GET /api/v1/users/{uid}** - Get specific user (requires auth)
8. **GET /api/v1/profile** - Get current user profile (requires auth)
9. **PUT /api/v1/profile** - Update current user profile (requires auth)
10. **GET /api/v1/settings** - Get user settings (requires auth)
11. **PUT /api/v1/settings** - Update user settings (requires auth)
12. **GET /api/v1/enrollments** - Get user enrollments (requires auth)
13. **POST /api/v1/enrollments** - Create enrollment (requires auth)
14. **GET /api/v1/payments/history** - Get payment history (requires auth)

### Admin-Only Endpoints (Requires Admin Role Token)
15. **POST /api/v1/courses** - Create course (admin only)
16. **GET /api/v1/admin/users** - Admin: View all users (admin only)

## Role-Based Access Control (RBAC)

### Test Users Available
| User Role | UID | Email |
|-----------|-----|-------|
| user | user123 | user@smartstudy.com |
| admin | admin456 | admin@smartstudy.com |

### Access Matrix

| Endpoint | Public | User Auth | Admin Auth | IDOR Risk | Notes |
|----------|--------|-----------|-----------|-----------|-------|
| /health | ✓ | ✓ | ✓ | No | Always accessible |
| POST /auth/login | ✓ | ✗ | ✗ | No | Public auth endpoint |
| GET /courses | ✓ | ✓ | ✓ | Low | Course listing, public data |
| GET /courses/{id} | ✓ | ✓ | ✓ | Low | Single course, public data |
| POST /auth/logout | ✗ | ✓ | ✓ | No | Requires token |
| GET /users | ✗ | ✓ | ✓ | Low | List all users (potential info leak) |
| GET /users/{uid} | ✗ | ✓ | ✓ | **HIGH** | No user validation - IDOR risk |
| GET /profile | ✗ | ✓ | ✓ | No | Returns own profile based on token |
| PUT /profile | ✗ | ✓ | ✓ | No | Updates own profile |
| GET /settings | ✗ | ✓ | ✓ | No | User settings |
| PUT /settings | ✗ | ✓ | ✓ | No | Update user settings |
| POST /courses | ✗ | ✗ | ✓ | No | Admin only - role check enforced |
| GET /admin/users | ✗ | ✗ | ✓ | No | Admin only - role check enforced |
| GET /enrollments | ✗ | ✓ | ✓ | **HIGH** | No user-level filtering - potential data leak |
| POST /enrollments | ✗ | ✓ | ✓ | No | Creates enrollments |
| GET /payments/history | ✗ | ✓ | ✓ | **HIGH** | No user-level filtering - potential data leak |

## Initial Risk Assessment

### High Priority Issues Identified During Discovery
1. **GET /users/{uid}** - No user validation; user can retrieve ANY user's data
2. **GET /enrollments** - No filtering by user; may return ALL enrollments
3. **GET /payments/history** - No filtering by user; may return ALL payments

These will be tested in the IDOR and Authorization test suites.
