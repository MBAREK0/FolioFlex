package org.mbarek0.folioflex.web.vm.request.translation;

import jakarta.validation.constraints.NotBlank;

public class TranslatorVm {


    @NotBlank(message = "Target language cannot be blank")
    private String targetLanguage;

    @NotBlank(message = "Source text cannot be blank")
    private String text;

    public TranslatorVm() {
    }

    public TranslatorVm(String targetLanguage, String text) {
        this.targetLanguage = targetLanguage;
        this.text = text;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public String getText() {
        return text;
    }
}
