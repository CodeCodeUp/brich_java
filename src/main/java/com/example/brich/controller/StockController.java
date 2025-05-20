package com.example.brich.controller;

import com.example.brich.model.AggregatedChange;
import com.example.brich.model.AggregatedChangeDto;
import com.example.brich.model.PricePointDto;
import com.example.brich.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService service;

    @GetMapping("/changes")
    public List<AggregatedChange> changes(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return service.getAggregatedChanges(start, end);
    }

    @GetMapping("/{code}/chart")
    public ChartResponse chart(
            @PathVariable String code
    ) {
        List<PricePointDto> prices = service.getPricePoints(code);
        List<AggregatedChangeDto> marks = service.getMarks(code);
        return new ChartResponse(prices, marks);
    }
}
