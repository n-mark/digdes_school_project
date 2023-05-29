package ru.digdes.school.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
@Builder
public class ExceptionDto {
    private Integer statusCode;
    private String message;
    private Date timestamp;
    private String description;
}
