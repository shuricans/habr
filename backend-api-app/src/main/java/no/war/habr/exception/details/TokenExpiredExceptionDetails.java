package no.war.habr.exception.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenExpiredExceptionDetails extends ExceptionDetails {

    private boolean expired;

}