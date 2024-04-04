package edu.java.bot.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private final Bucket bucket;
    private static final int STATUS_CODE = 429;

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest req,
        @NotNull HttpServletResponse res,
        @NotNull FilterChain filterChain
    ) throws IOException, ServletException {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(req, res);
        } else {
            res.setStatus(STATUS_CODE);
        }
    }
}
