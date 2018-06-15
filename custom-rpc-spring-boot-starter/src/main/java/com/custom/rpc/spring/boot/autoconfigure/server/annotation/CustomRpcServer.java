package com.custom.rpc.spring.boot.autoconfigure.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.custom.rpc.spring.boot.autoconfigure.server.config.CustomRpcMarkerConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomRpcMarkerConfiguration.class)
public @interface CustomRpcServer {

}
