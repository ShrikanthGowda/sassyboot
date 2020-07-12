package com.cksutil.sassyboot.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {
    private HttpStatus status;
    private String message;
    private Object payload;
    private Object errors;

    public ApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ApiResponse success(String message){
       return new ApiResponse(HttpStatus.OK,message);
    }

    public static ApiResponse success(String message,Object payload){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK,message);
        apiResponse.payload = payload;
        return apiResponse;
    }

    public static ApiResponse failure(HttpStatus status, String message){
        return new ApiResponse(status,message);
    }
}
