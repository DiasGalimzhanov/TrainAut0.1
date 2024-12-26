package com.example.trainaut01.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstructionPageData {
    private final String resourcePath;
    private final String text;
    private final boolean isLottie;
}
