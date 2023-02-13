package uz.md.shopappjdbc.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ErrorData {

    private String devMsg;
    private String userMsg;

    private Integer errorCode;

    private String fieldName;

    public ErrorData(String devMsg,String userMsg, Integer errorCode) {
        this.devMsg = devMsg;
        this.userMsg = userMsg;
        this.errorCode = errorCode;
    }
}
