package com.packtpub.storm.trident.operator;

import com.packtpub.storm.model.FixMessageDto;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class MessageTypeFilter extends BaseFilter {
    private static final long serialVersionUID = 1L;
//    private static final Logger LOG = LoggerFactory.getLogger(MessageTypeFilter.class);

    @Override
    public boolean isKeep(TridentTuple tuple) {
        FixMessageDto message = (FixMessageDto) tuple.getValue(0);
        if (message.msgType.equals("8") && message.price > 0) {
            return true;
        }
        return false;
    }
}