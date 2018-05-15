package forex_guru.mappers;

import forex_guru.model.external.ExchangeRate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface DataMapper {

    @Insert("INSERT INTO `ForexGuru`.`rates` " +
            "(`date`, `timestamp`, `symbol`, `close`) " +
            "VALUES (#{date}, #{timestamp}, #{symbol}, #{close}); ")
    public boolean insertRate(ExchangeRate rate);

    @Select("SELECT * FROM `ForexGuru`.`rates` WHERE `symbol` = #{symbol} ")
    public ExchangeRate[] findRatesBySymbol(String symbol);

    @Select("SELECT * FROM `ForexGuru`.`rates` WHERE `symbol` = \"${symbol}\" AND `timestamp` = \"${timestamp}\" LIMIT 1")
    public ExchangeRate findRateBySymbolAndTimestamp(@Param("symbol") String symbol, @Param("timestamp") long timestamp);

    @Select("SELECT timestamp FROM `ForexGuru`.`rates` WHERE `symbol` = #{symbol} ORDER BY `timestamp` DESC LIMIT 1")
    public long findLatestTimestampBySymbol(String symbol);
}
