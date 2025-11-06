package com.logistechpro.Service;

import com.logistechpro.DTO.Request.PORequest;
import com.logistechpro.DTO.Response.POResponse;

public interface POService {
        POResponse create(PORequest request);
        POResponse approvePurchaseOrder(Long poId);
        POResponse receivePurchaseOrder(Long poId);
        POResponse cancelPurchaseOrder(Long poId);
}
