package dev.thomazz.pledge.network.queue;

import dev.thomazz.pledge.network.NetworkMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Setter
@Getter
public class MessageQueueHandler extends ChannelOutboundHandlerAdapter {
    private final Deque<NetworkMessage> messageQueue = new ConcurrentLinkedDeque<>();
    private QueueMode mode = QueueMode.PASS;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        switch (this.mode) {
            case ADD_FIRST:
                this.messageQueue.addFirst(NetworkMessage.of(msg, promise));
                break;
            case ADD_LAST:
                this.messageQueue.addLast(NetworkMessage.of(msg, promise));
                break;
            default:
            case PASS:
                super.write(ctx, msg, promise);
                break;
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.drain(ctx);
        super.close(ctx, promise);
    }

    public void drain(ChannelHandlerContext ctx) {
        while (!this.messageQueue.isEmpty()) {
            NetworkMessage message = this.messageQueue.poll();
            ctx.write(message.getMessage(), message.getPromise());
        }

        ctx.flush();
    }
}
