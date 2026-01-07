package com.logistechpro.service;

import com.logistechpro.dto.request.PORequest;
import com.logistechpro.dto.response.POResponse;

public interface POService {
        POResponse create(PORequest request);
        POResponse approvePurchaseOrder(Long poId);
        POResponse receivePurchaseOrder(Long poId);
        POResponse cancelPurchaseOrder(Long poId);
}
