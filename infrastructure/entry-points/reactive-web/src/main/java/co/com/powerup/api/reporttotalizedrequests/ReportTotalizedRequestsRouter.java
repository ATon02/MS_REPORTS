package co.com.powerup.api.reporttotalizedrequests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.config.JwtAuthenticationFilter;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.List;

@Configuration
public class ReportTotalizedRequestsRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFunctionReportTotalizedRequests(ReportTotalizedRequestsHandler handler, JwtAuthenticationFilter filter) {
        RouterFunction<ServerResponse> reportTotalizedRequests = route(GET("/api/v1/reports"), handler::getReportTotalizedRequests)
                .filter(filter.requireRole(List.of("admin")));
        return reportTotalizedRequests;
    }
}
