package xyz.pwmw.mynlife.configuration.web;


import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.pwmw.mynlife.service.PricingPlanService;
import xyz.pwmw.mynlife.util.jwt.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j2
@RequiredArgsConstructor
public class HttpInterceptor extends HandlerInterceptorAdapter {
	PricingPlanService pricingPlanService = new PricingPlanService();
	private  final JwtTokenProvider jwtTokenProvider;
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response,
							 Object handler) {

		String jwt = request.getHeader("authorization");
		log.info("================ Before Method");
		log.info("접속 ip 주소 '{}'", request.getRemoteAddr());
		log.info(request.getRemoteAddr());
		if(tokenFilter(request)){
			if(jwt != null && !jwt.equals("undefined")){
				Bucket bucket = pricingPlanService.jwtBucket(jwt);
				log.info("jwt값 : '{}'", jwt);
				if(!jwtTokenProvider.validateToken(jwt)){
					log.info("토큰만료 : '{}'", jwt);
					response.setStatus(401);
					return false;
				}
				return validOverTraffic(bucket, jwt);
			}
		}
		Bucket bucket = pricingPlanService.resolveBucket(request);
		return validOverTraffic(bucket, request.getRemoteAddr());
	}
	@Override
	public void postHandle( HttpServletRequest request,
							HttpServletResponse response,
							Object handler,
							ModelAndView modelAndView) {
		log.info("================ Method Executed");
	}
	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response,
								Object handler,
								Exception ex) {
		log.info("================ Method Completed");
	}
	private boolean validOverTraffic(Bucket bucket, String data){
		if (bucket.tryConsume(1)) { // 1개 사용 요청
			// 초과하지 않음
			return true;
		} else {
			// 제한 초과
			log.info("{} 트래픽 초과!!!", data);
			return false;
		}
	}

	public boolean tokenFilter(HttpServletRequest re){
		log.info("얘가 먼저여야하는데...");
		if(re.getRequestURI().contains("signIn") ||
				re.getRequestURI().contains("api/v1/login") ||
				re.getRequestURI().contains("api/v1/signUp") ||
				re.getRequestURI().contains("api/v1/social/access") ||
				re.getRequestURI().contains("api/v1/sendEmailForAuthEmail") ||
				re.getRequestURI().contains("region/city") ||
				re.getRequestURI().contains("region/region")){
			log.info("담겨있음");
			return false;
		}
		return true;
	}
}