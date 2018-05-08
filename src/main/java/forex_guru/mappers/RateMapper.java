package forex_guru.mappers;

import forex_guru.model.kibot.KibotRate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RateMapper {

    @Insert("INSERT INTO `ForexGuru`.`rates` " +
            "(`date`, `timestamp`, `symbol`, `close`) " +
            "VALUES (#{date}, #{timestamp}, #{symbol}, #{close}); ")
    public boolean insertRate(KibotRate rate);


    @Select("SELECT * FROM `ForexGuru`.`rates` WHERE `symbol` = #{symbol} ")
    public KibotRate[] findRateBySymbol(String symbol);
}
