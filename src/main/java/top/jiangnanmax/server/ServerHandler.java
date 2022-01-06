package top.jiangnanmax.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.jiangnanmax.util.StringUtil;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg == null || "".equals(msg)) {
            ctx.channel().writeAndFlush(StringUtil.USAGE);
            return;
        }
        String command = StringUtil.getCommand(msg);
        if ("get".equals(command)) {
            String key = StringUtil.getKey(msg);
            if (key == null) {
                ctx.channel().writeAndFlush(StringUtil.USAGE);
            } else {
                String value = Server.db.get(key);
                ctx.channel().writeAndFlush(value == null ? "nil\r\n" : value + "\r\n");
            }
        } else if ("put".equals(command)) {
            String[] kv = StringUtil.getKV(msg);
            if (kv == null) {
                ctx.channel().writeAndFlush(StringUtil.USAGE);
            } else {
                Server.db.put(kv[0], kv[1]);
                ctx.channel().writeAndFlush("ok\r\n");
            }
        } else if ("del".equals(command)) {
            String key = StringUtil.getKey(msg);
            if (key == null) {
                ctx.channel().writeAndFlush(StringUtil.USAGE);
            } else {
                boolean ok = Server.db.delete(key);
                ctx.channel().writeAndFlush(ok ? "ok\r\n" : "nil\r\n");
            }
        } else {
            ctx.channel().writeAndFlush(StringUtil.USAGE);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("client connected, remoteAddress: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("client disconnected, remoteAddress: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("remoteAddress: {}, cause: {}", ctx.channel().remoteAddress(), cause.getMessage());
        ctx.channel().writeAndFlush("exception! cause: " + cause.getMessage() + "\r\n");
    }
}
