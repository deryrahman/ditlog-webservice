package id.ac.itb.logistik.ditlog.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class JwtFilter extends GenericFilterBean {

  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
      throws IOException, ServletException {

    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;
    final String authHeader = request.getHeader("authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new ServletException("Missing or invalid Authorization header");
    }

    final String token = authHeader.substring(7);

    try {
      final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token)
          .getBody();
      request.setAttribute("claims", claims);
    } catch (final SignatureException e) {
      throw new ServletException("Invalid token");
    }

    chain.doFilter(req, res);
  }
}
