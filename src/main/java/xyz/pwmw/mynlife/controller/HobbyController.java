package xyz.pwmw.mynlife.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.pwmw.mynlife.service.HobbyService;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "취미와 관련된 목록")
@RequestMapping("api/hobby")
public class HobbyController {
    private final HobbyService hobbyService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "취미 목록들을 반환", response = List.class)
    })
    @ApiOperation(value = "취미 리스트들을 반환해주는 api", notes = "")
    @GetMapping("/getAllHobby")
    public ResponseEntity<?> getAllHobby(){
        return new ResponseEntity<>(hobbyService.findAll(), HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "취미 견적서 반환", response = List.class)
    })
    @ApiOperation(value = "취미 입문에 대한 견적서를 반환해주는 api", notes = "")
    @GetMapping("/getHobbyCost")
    public ResponseEntity<?> getHobbyCost(@RequestParam long id){
        return new ResponseEntity<>(hobbyService.getIntoHobbyCostData(id), HttpStatus.OK);
    }
}
