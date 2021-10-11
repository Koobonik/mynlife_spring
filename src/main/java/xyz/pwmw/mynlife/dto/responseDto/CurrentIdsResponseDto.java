package xyz.pwmw.mynlife.dto.responseDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentIdsResponseDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "bookId", value = "bookId")
    private int bookId;

    @ApiModelProperty(example = "educationId", value = "educationId")
    private int educationId;

    @ApiModelProperty(example = "galleryId", value = "galleryId")
    private int galleryId;

    @ApiModelProperty(example = "etcId", value = "etcId")
    private int etcId;

    @ApiModelProperty(example = "newsId", value = "newsId")
    private int newsId;
}
