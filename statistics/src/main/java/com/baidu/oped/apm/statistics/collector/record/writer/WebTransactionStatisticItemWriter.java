package com.baidu.oped.apm.statistics.collector.record.writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.oped.apm.common.jpa.entity.QWebTransactionStatistic;
import com.baidu.oped.apm.common.jpa.entity.StatisticType;
import com.baidu.oped.apm.common.jpa.entity.WebTransactionStatistic;
import com.baidu.oped.apm.common.jpa.repository.WebTransactionStatisticRepository;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Created by mason on 8/31/15.
 */
@Component
public class WebTransactionStatisticItemWriter extends BaseWriter<WebTransactionStatistic> {

    @Autowired
    private WebTransactionStatisticRepository webTransactionStatisticRepository;

    @Override
    protected void writeItem(WebTransactionStatistic item) {
        QWebTransactionStatistic qWebTransactionStatistic = QWebTransactionStatistic.webTransactionStatistic;
        BooleanExpression appIdCondition = qWebTransactionStatistic.transactionId.eq(item.getTransactionId());
        BooleanExpression periodCondition = qWebTransactionStatistic.period.eq(item.getPeriod());
        BooleanExpression timestampCondition = qWebTransactionStatistic.timestamp.eq(item.getTimestamp());

        BooleanExpression whereCondition = appIdCondition.and(periodCondition).and(timestampCondition);
        WebTransactionStatistic existStatistic = webTransactionStatisticRepository.findOne(whereCondition);

        if (existStatistic == null) {
            webTransactionStatisticRepository.save(item);
        } else {
            copyStatisticValue(item, existStatistic);
            webTransactionStatisticRepository.saveAndFlush(existStatistic);
        }
    }

    @Override
    protected StatisticType getStatisticType() {
        return StatisticType.WEB_TRANSACTION;
    }
}
