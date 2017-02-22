package com.xzy.cm.common.helper;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.lang.management.ManagementFactory;

/**
 * 做性能统计是需要用
 *
 */
public class PerformanceLog {
    //
    protected static Logger slog = Logger.getLogger("PERFORMANCE");
    //
    public static final String PERF_CPU = "cpu_time";
    public static final String PERF_TIME = "exec_time";
    public static final String PERF_MEM = "mem_count";
    //
    public static int TIME = 0;
    public static int MEMORY = 1;
    public static int CPU = 2;
    //

    /**
     * @return
     */
    public static long[] start() {
        long[] start = new long[3];
        start[TIME] = System.currentTimeMillis();
        start[MEMORY] = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        start[CPU] = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
        //
        return start;
    }

    /**
     * @param start
     * @param msg
     */
    public static void info(long[] start, Object msg) {
        long[] sub = cal(start);
        put(sub);
        slog.info(msg);
        clean();
    }

    public static void debug(long[] start, Object msg) {
        long[] sub = cal(start);
        put(sub);
        slog.debug(msg);
        clean();
    }

    public static void error(long[] start, Object msg) {
        long[] sub = cal(start);
        put(sub);
        slog.error(msg);
        clean();
    }

    public static long[] cal(long[] start) {
        long[] end = new long[3];
        end[TIME] = System.currentTimeMillis();
        end[MEMORY] = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        end[CPU] = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
        //
        end[TIME] = end[TIME] - start[TIME];
        end[MEMORY] = end[MEMORY] - start[MEMORY];
        end[CPU] = (end[CPU] - start[CPU]) / 1000000;
        //
        return end;
    }

    private static void put(long[] args) {//没判空，private一下
        MDC.put(PERF_CPU, args[CPU]);
        MDC.put(PERF_TIME, args[TIME]);
        MDC.put(PERF_MEM, args[MEMORY]);
    }

    private static void clean() {
        MDC.remove(PERF_CPU);
        MDC.remove(PERF_TIME);
        MDC.remove(PERF_MEM);
    }
}
