package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NearbyPlaceResult {
    List<NearbyPlaceDTO> places;
    boolean success;
    String errorCode;      // e.g., "TIMEOUT", "KEY_EXPIRED", "QUOTA_EXCEEDED", "NETWORK_ERROR"
    String errorMessage;

    public static NearbyPlaceResult success(List<NearbyPlaceDTO> places) {
        return NearbyPlaceResult.builder()
                .places(places)
                .success(true)
                .build();
    }

    public static NearbyPlaceResult failure(String errorCode, String errorMessage) {
        return NearbyPlaceResult.builder()
                .places(List.of())
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public boolean shouldFallback() {
        return !success || places.isEmpty();
    }
}