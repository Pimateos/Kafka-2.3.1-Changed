package org.apache.kafka.common.requests;

import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.CommonFields;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;

import java.util.Collections;
import java.util.Map;

import static org.apache.kafka.common.protocol.CommonFields.NULLABLE_TRANSACTIONAL_ID;
import static org.apache.kafka.common.protocol.CommonFields.ERROR_CODE;
import static org.apache.kafka.common.protocol.types.Type.*;

public class NackProduceResponse extends AbstractResponse {
    private static final String TIMEOUT_KEY_NAME = "timeout";

    private static final Schema NACK_PRODUCE_REQUEST_V0 = new Schema(
        ERROR_CODE,
        new Field(TIMEOUT_KEY_NAME, INT32, "The time to await a response in ms."),
        CommonFields.NULLABLE_TRANSACTIONAL_ID
    );

    // If more schemas are implemented
    public static Schema[] schemaVersions() {
        return new Schema[] {NACK_PRODUCE_REQUEST_V0};
    }

    private final String transactionalId;
    private final int timeout;
    private Errors error;

    public NackProduceResponse(Errors error, int timeout, String transactionalId) {
        this.error = error;
        this.timeout = timeout;
        this.transactionalId = transactionalId;
    }

    public NackProduceResponse(Struct struct) {
        this.error = Errors.forCode(struct.get(ERROR_CODE));
        this.timeout = struct.getInt(TIMEOUT_KEY_NAME);
        this.transactionalId = struct.getOrElse(NULLABLE_TRANSACTIONAL_ID, null);
    }

    public Errors error() {
        return error;
    }

    public int timeout() {
        return timeout;
    }

    @Override
    public Map<Errors, Integer> errorCounts() {
        return Collections.singletonMap(error(), 1);
    }

    @Override
    public Struct toStruct(short version){
        Struct struct = new Struct(ApiKeys.NACK_PRODUCE_REQUEST.responseSchema(version));
        struct.set(TIMEOUT_KEY_NAME, timeout);
        struct.setIfExists(NULLABLE_TRANSACTIONAL_ID, transactionalId);
        struct.set(ERROR_CODE, error.code());
        return struct;
    }
}