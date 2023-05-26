package com.samitiapp.api.samiti.samiti.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public final class Samiti {
    public static String table = "samiti";
    private final String id;
    private final String name;
    private final String adminId;

    private long createdAt = System.currentTimeMillis();


    public Samiti(String id, String name, String adminId) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Samiti) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.adminId, that.adminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, adminId);
    }

    @Override
    public String toString() {
        return "Samiti[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "adminId=" + adminId + ']';
    }


}
