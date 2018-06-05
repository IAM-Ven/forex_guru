package forex_guru.controllers;

import forex_guru.services.IndicatorService;
import forex_guru.services.SignalService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ta4j.core.Decimal;


import java.util.HashMap;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuruControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GuruController guruController;

    @Mock
    private SignalService signalService;

    @Mock
    private IndicatorService indicatorService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(guruController)
                .build();
    }

    @Test
    public void signalScan() throws Exception {

        HashMap<String, Float> signals = new HashMap<>();
        signals.put("eurusd", 1.0f);
        signals.put("usdjpy", 2.0f);

        when(signalService.scanSignals()).thenReturn(signals);

        mockMvc.perform(get("/signals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data.eurusd", comparesEqualTo(1.0)))
                .andExpect(jsonPath("$.data.usdjpy", comparesEqualTo(2.0)));

    }

    @Test
    public void dailyIndicator() throws Exception {

        Decimal indicator = Decimal.valueOf(3.0);

        when(indicatorService.calculateDailyIndicator("sma", "eurusd", 30)).thenReturn(indicator);

        mockMvc.perform(get("/dailyindicator")
                .param("type", "sma")
                .param("symbol", "eurusd")
                .param("trailing", "30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data", comparesEqualTo(3.0)));

    }
}