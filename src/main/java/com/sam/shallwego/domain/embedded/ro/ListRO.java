package com.sam.shallwego.domain.embedded.ro;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListRO<T> {

    private final List<T> data;

}
