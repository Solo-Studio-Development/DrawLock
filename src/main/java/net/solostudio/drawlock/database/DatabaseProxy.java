package net.solostudio.drawlock.database;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DatabaseProxy {
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(@NotNull final Class<T> clazz, final T instance) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new DatabaseInvocationHandler(instance)
        );
    }

    private record DatabaseInvocationHandler(@NotNull Object instance) implements InvocationHandler {
        @Override
        public Object invoke(@NotNull final Object proxy, @NotNull final Method method, @NotNull final Object[] args) throws Throwable {
            if (method.getReturnType().equals(Void.TYPE)) {
                DrawLock.getInstance().getScheduler().runTaskAsynchronously(() -> {
                    try {
                        method.invoke(instance, args);
                    } catch (Exception ignored) {
                        LoggerUtils.error("An error occurred while invoking a method asynchronously.");
                    }
                });
                return null;
            }

            return method.invoke(instance, args);
        }
    }
}
