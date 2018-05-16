package forex_guru.mappers;

import forex_guru.model.internal.Indicator;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IndicatorMapper {

    @Insert("INSERT INTO `ForexGuru`.`indicators` " +
            "(`date`, `symbol`, `close`, `change`, `simpleMovingAverage`, `exponentialMovingAverage`) " +
            "VALUES (#{date}, #{symbol}, #{close}, #{change}, #{simpleMovingAverage}, #{exponentialMovingAverage});")
    public boolean insertIndicator(Indicator indicator);

    @Select("SELECT * FROM `ForexGuru`.`indicators` WHERE `symbol` = \"${symbol}\" AND `date` = \"${date}\" LIMIT 1")
    public Indicator findIndicatorBySymbolAndDate(@Param("symbol") String symbol, @Param("date") String date);

}
