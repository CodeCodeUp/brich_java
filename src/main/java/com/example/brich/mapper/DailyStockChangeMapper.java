package com.example.brich.mapper;


import com.example.brich.model.AggregatedChangeDto;
import com.example.brich.model.PricePointDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyStockChangeMapper {
    List<AggregatedChangeDto> findAggregatedChangesByDay(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("changeType") String changeType
    );

    List<PricePointDto> findPricePoints(
            @Param("code") String code
    );

    List<AggregatedChangeDto> findMarks(
            @Param("code") String code
    );
}
