package com.logistechpro.service;

import com.logistechpro.dto.Request.PORequest;
import com.logistechpro.dto.Response.POResponse;

public interface POService {
        POResponse create(PORequest request);
        POResponse approvePurchaseOrder(Long poId);
        POResponse receivePurchaseOrder(Long poId);
        POResponse cancelPurchaseOrder(Long poId);
}
