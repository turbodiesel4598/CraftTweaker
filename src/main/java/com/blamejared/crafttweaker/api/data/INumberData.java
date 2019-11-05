package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.nbt.NumberNBT;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("crafttweaker.api.data.INumberData")
public interface INumberData extends IData {
    
    default long getLong() {
        return getInternal().getLong();
    }
    
    default int getInt() {
        return getInternal().getInt();
    }
    
    default short getShort() {
        return getInternal().getShort();
    }
    
    default byte getByte() {
        return getInternal().getByte();
    }
    
    default double getDouble() {
        return getInternal().getDouble();
    }
    
    default float getFloat() {
        return getInternal().getFloat();
    }
    
    @Override
    NumberNBT getInternal();

    @Override
    default String toJsonString() {
        return String.valueOf(getInternal().getAsNumber());
    }
}
