/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtstack.flinkx.kafka09.writer;

import com.dtstack.flinkx.config.DataTransferConfig;
import com.dtstack.flinkx.config.WriterConfig;
import com.dtstack.flinkx.writer.DataWriter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.DtOutputFormatSinkFunction;
import org.apache.flink.types.Row;

import java.util.Map;
import java.util.Set;

import static com.dtstack.flinkx.kafka09.KafkaConfigKeys.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/7/4
 */
public class Kafka09Writer extends DataWriter {

    private String timezone;

    private String encoding;

    private String topic;

    private String brokerList;

    private Map<String, Map<String, String>> topicSelect;

    private Set<Map.Entry<String, Map<String, String>>> entryTopicSelect;

    private Map<String, String> producerSettings;

    public Kafka09Writer(DataTransferConfig config) {
        super(config);
        WriterConfig writerConfig = config.getJob().getContent().get(0).getWriter();
        timezone = writerConfig.getParameter().getStringVal(KEY_TIMEZONE);
        encoding = writerConfig.getParameter().getStringVal(KEY_ENCODING, "utf-8");
        topic = writerConfig.getParameter().getStringVal(KEY_TOPIC);
        brokerList = writerConfig.getParameter().getStringVal(KEY_BROKER_LIST);
        topicSelect = (Map<String, Map<String, String>>) writerConfig.getParameter().getVal(KEY_TOPIC_SELECT);
        entryTopicSelect = (Set<Map.Entry<String, Map<String, String>>>) writerConfig.getParameter().getVal(KEY_ENTRY_TOPIC_SELECT);
        producerSettings = (Map<String, String>) writerConfig.getParameter().getVal(KEY_PRODUCER_SETTINGS);
    }

    @Override
    public DataStreamSink<?> writeData(DataStream<Row> dataSet) {
        Kafka09OutputFormat format = new Kafka09OutputFormat();
        format.setTimezone(timezone);
        format.setEncoding(encoding);
        format.setTopic(topic);
        format.setBrokerList(brokerList);
        format.setTopicSelect(topicSelect);
        format.setEntryTopicSelect(entryTopicSelect);
        format.setProducerSettings(producerSettings);

        DtOutputFormatSinkFunction sinkFunction = new DtOutputFormatSinkFunction(format);
        DataStreamSink<?> dataStreamSink = dataSet.addSink(sinkFunction);

        dataStreamSink.name("kafka09writer");
        return null;
    }
}
