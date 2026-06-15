import json
import os
import time

import pandas as pd


def load(path):
    with open(path) as f:
        return json.load(f)


FRAMEWORKS = {
    "spring": {
        "name": "Spring Boot (Kotlin/Java)",
        "auth_middleware": """// Add security interceptor to enforce auth
@Configuration
@EnableWebSecurity
class SecurityConfig(private val authFilter: JwtAuthenticationFilter) : SecurityFilterChain {
    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/v1/public/**").permitAll()
            .antMatchers("/api/v1/**").authenticated()
            .and()
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")?.substringAfter("Bearer ")
        if (token != null) {
            try {
                val decodedJWT = JWT.require(Algorithm.HMAC256("secret")).build().verify(token)
                val auth = UsernamePasswordAuthenticationToken(decodedJWT.subject, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: JWTVerificationException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        }
        chain.doFilter(request, response)
    }
}"""
    },
    "express": {
        "name": "Express.js (Node.js)",
        "auth_middleware": """// Middleware to enforce JWT auth
const verifyToken = (req, res, next) => {
  const token = req.headers.authorization?.split(" ")[1];
  if (!token) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    return res.status(403).json({ error: "Forbidden" });
  }
};

app.get("/api/v1/profile", verifyToken, (req, res) => {
  res.json({ user: req.user });
});"""
    },
    "django": {
        "name": "Django (Python)",
        "auth_middleware": """# settings.py
REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'rest_framework_jwt.authentication.JSONWebTokenAuthentication',
    ],
    'DEFAULT_PERMISSION_CLASSES': [
        'rest_framework.permissions.IsAuthenticated',
    ]
}

# views.py
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def protected_view(request):
    return Response({"user": request.user.username})"""
    }
}


def suggest_remediation(rec):
    cat = rec.get("test_category")
    endpoint = rec.get("endpoint")
    status = rec.get("status")
    role = rec.get("role")
    rt = rec.get("response_time_ms") or 0

    if cat == "auth_bypass":
        if status and 200 <= status < 300:
            return f"CRITICAL: Auth bypass at {endpoint} (HTTP {status} without auth). Apply framework security middleware (Spring: @EnableWebSecurity, Express: jwt middleware, Django: IsAuthenticated). Add token validation, return 401 for missing tokens."
        else:
            return f"OK: {endpoint} properly requires authentication (HTTP {status})"

    if cat == "auth_validation":
        if role == "none":
            return f"INFO: {endpoint} not accessible unauthenticated (expected)"
        if not (status and 200 <= status < 300):
            return f"WARNING: {endpoint} failed for role '{role}' (HTTP {status}). Check: token expiry (jwt.verify), scopes (audience claim), RBAC rules (@PreAuthorize in Spring), role permissions. Use @PreAuthorize('hasRole(\"ADMIN\")') or equivalent middleware."
        return f"OK: Authenticated {role} access successful"

    if rt > 2000:
        return f"PERFORMANCE: {endpoint} slow ({rt}ms). Add caching (@Cacheable in Spring, redis in Express, @cache_page in Django), optimize DB queries (add indexes, use select_related), implement pagination."

    return "NO ISSUE: Test passed"


def main():
    root = os.path.dirname(__file__)
    report_path = os.path.join(root, "report.json")
    if not os.path.exists(report_path):
        print("report.json not found — run run_tests_full.py first")
        return

    records = load(report_path)
    rems = []
    for r in records:
        if not r.get("finding") and r.get("role") == "none":
            continue
        suggestion = suggest_remediation(r)
        out = dict(r)
        out["remediation"] = suggestion
        out["reviewed_at"] = time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime())
        rems.append(out)

    out_json = os.path.join(root, "report_remediation_detailed.json")
    with open(out_json, "w") as f:
        json.dump(rems, f, indent=2)

    out_xlsx = os.path.join(root, "report_remediation_detailed.xlsx")
    if rems:
        df = pd.DataFrame(rems)
        # Truncate long remediation for Excel display
        df['remediation'] = df['remediation'].str[:250]
        df.to_excel(out_xlsx, index=False, engine="openpyxl")
        print(f"Wrote {len(rems)} detailed remediation rows")
        print(f"  JSON (full): {out_json}")
        print(f"  Excel (summary): {out_xlsx}")
    else:
        print("No remediation rows generated")


if __name__ == '__main__':
    main()
