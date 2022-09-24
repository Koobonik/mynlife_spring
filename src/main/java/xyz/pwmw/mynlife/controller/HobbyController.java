package xyz.pwmw.mynlife.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "취미와 관련된 목록")
@RequestMapping("api/v1")
public class HobbyController {

}
