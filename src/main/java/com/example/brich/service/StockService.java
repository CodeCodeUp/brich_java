package com.example.brich.service;

import com.example.brich.mapper.DailyStockChangeMapper;
import com.example.brich.model.AggregatedChange;
import com.example.brich.model.AggregatedChangeDto;
import com.example.brich.model.DailyStockChange;
import com.example.brich.model.PricePointDto;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {
    @Autowired
    private DailyStockChangeMapper mapper;

    public List<AggregatedChange> getAggregatedChanges(LocalDate start, LocalDate end) {
        List<AggregatedChangeDto> rawList = mapper.findAggregatedChangesByDay(start, end);
        // 1. 按 (tradeDate, stockCode, stockName) 分组
        Map<List<Object>, List<AggregatedChangeDto>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(item ->
                        Arrays.asList(item.getTradeDate(), item.getStockCode(), item.getStockName())
                ));

        // 2. 对每组做汇总
        List<AggregatedChange> result = new ArrayList<>();
        for (Map.Entry<List<Object>, List<AggregatedChangeDto>> entry : grouped.entrySet()) {
            List<Object> key = entry.getKey();
            LocalDate date = (LocalDate) key.get(0);
            String code   = (String)   key.get(1);
            String name   = (String)   key.get(2);

            BigDecimal incSum = BigDecimal.ZERO;
            BigDecimal decSum = BigDecimal.ZERO;
            Set<String> names = new LinkedHashSet<>();
            Set<String> poses = new LinkedHashSet<>();

            for (AggregatedChangeDto rec : entry.getValue()) {
                // 累加增持/减持
                if ("增持".equals(rec.getChangeType())) {
                    incSum = incSum.add(rec.getTotalPrice());
                } else if ("减持".equals(rec.getChangeType())) {
                    decSum = decSum.add(rec.getTotalPrice());
                }
                // 收集不重复的姓名和职位
                names.add(rec.getChangerName());
                poses.add(rec.getChangerPosition());
            }

            AggregatedChange dto = new AggregatedChange();
            dto.setTradeDate(date);
            dto.setStockCode(code);
            dto.setStockName(name);
            dto.setTotalIncrease(incSum);
            dto.setTotalDecrease(decSum);
            // 用逗号拼接
            dto.setChangerName(String.join(",", names));
            dto.setChangerPosition(String.join(",", poses));

            result.add(dto);
        }

        // 3. 按日期倒序排序
        result.sort(Comparator.comparing(AggregatedChange::getTradeDate).reversed());
        return result;
    }

    public List<PricePointDto> getPricePoints(String code) {
        return mapper.findPricePoints(code);
    }

    public List<AggregatedChangeDto> getMarks(String code) {
        return mapper.findMarks(code);
    }
}
