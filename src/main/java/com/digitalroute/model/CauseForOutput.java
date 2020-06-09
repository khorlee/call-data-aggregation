package com.digitalroute.model;

/**
 * @author Khor Lee Yong
 */
public enum CauseForOutput
{
    ONGOING_CALL((byte) 1),

    END_CALL((byte) 2),

    INCOMPLETE((byte) 0);

    private byte id;

    CauseForOutput(final byte outputId)
    {
        id = outputId;
    }

    public byte getId()
    {
        return id;
    }

    public static CauseForOutput findCauseForOutput(final byte pId)
    {
        for (CauseForOutput outputId : CauseForOutput.values()) {
            if (outputId.getId() == pId) {
                return outputId;
            }
        }
        return null;
    }
}
