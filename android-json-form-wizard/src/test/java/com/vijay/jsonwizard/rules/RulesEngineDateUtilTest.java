package com.vijay.jsonwizard.rules;

import com.vijay.jsonwizard.BaseTest;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;

import static com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT;


public class RulesEngineDateUtilTest extends BaseTest {

    private RulesEngineDateUtil rulesEngineDateUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rulesEngineDateUtil = new RulesEngineDateUtil();
    }

    @Test
    public void testGetDifferenceDaysBtwTodayAndSaidDateShouldCalculateCorrectly() throws ParseException {
        RulesEngineDateUtil spyRulesEngineDateUtil = Mockito.spy(rulesEngineDateUtil);
        Mockito.doReturn(LocalDate.fromDateFields(DATE_FORMAT.parse("07-04-2020")).toDate().getTime()).when(spyRulesEngineDateUtil).getTimeInMillis();
        long result = spyRulesEngineDateUtil.getDifferenceDays("06-04-2020");
        Assert.assertEquals(1, result);
    }

    @Test
    public void testGetDifferenceDaysBtwDateAAndNullDateShouldReturn0() {
        long result = rulesEngineDateUtil.getDifferenceDays(null, "07-04-2020");
        Assert.assertEquals(0, result);
    }

    @Test
    public void testGetDifferenceDaysBtwTodayAndNullDateShouldReturn0() {
        long result = rulesEngineDateUtil.getDifferenceDays(null);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testGetDifferenceDaysBtwDateAndSaidDateShouldReturn1() {
        long result = rulesEngineDateUtil.getDifferenceDays("06-04-2020", "07-04-2020");
        Assert.assertEquals(1, result);
    }

    @Test
    public void testAddDurationShouldAddSpecifiedDuration() {
        Assert.assertEquals("07-04-2020", rulesEngineDateUtil.addDuration("06-04-2020", "1d"));//should add one day
        Assert.assertEquals("13-04-2020", rulesEngineDateUtil.addDuration("06-04-2020", "1w"));//should add one week
        Assert.assertEquals("06-05-2020", rulesEngineDateUtil.addDuration("06-04-2020", "1m"));//should add one month
        Assert.assertEquals("06-04-2021", rulesEngineDateUtil.addDuration("06-04-2020", "1y"));//should add one yr
    }

    @Test
    public void testSubtractDurationShouldSubtractSpecifiedDuration() {
        Assert.assertEquals("05-04-2020", rulesEngineDateUtil.subtractDuration("06-04-2020", "1d"));//should minus one day
        Assert.assertEquals("30-03-2020", rulesEngineDateUtil.subtractDuration("06-04-2020", "1w"));//should minus one week
        Assert.assertEquals("06-03-2020", rulesEngineDateUtil.subtractDuration("06-04-2020", "1m"));//should minus one month
        Assert.assertEquals("06-04-2019", rulesEngineDateUtil.subtractDuration("06-04-2020", "1y"));//should minus one yr
    }
}