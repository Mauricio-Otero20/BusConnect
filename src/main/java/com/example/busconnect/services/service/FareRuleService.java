package com.example.busconnect.services.service;

import com.example.busconnect.api.dto.FareRuleDtos.*;

import java.math.BigDecimal;
import java.util.List;
public interface FareRuleService {

    FareRuleResponse createFareRule(FareRuleCreateRequest request);

    FareRuleResponse updateFareRule(Long id, FareRuleUpdateRequest request);

    FareRuleResponse getFareRuleById(Long id);

    List<FareRuleResponse> getAllFareRules();

    List<FareRuleResponse> getFareRulesByRouteId(Long routeId);

    FareRuleResponse getFareRuleForSegment(Long routeId, Long fromStopId, Long toStopId);

    List<FareRuleResponse> getDynamicPricingRules(Long routeId);

    void deleteFareRule(Long id);

    BigDecimal calculateDynamicPrice(Long fareRuleId, Double occupancyRate);
}
