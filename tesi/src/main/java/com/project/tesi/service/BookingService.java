package com.project.tesi.service;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    int migrateFakeMeetLinks();
}