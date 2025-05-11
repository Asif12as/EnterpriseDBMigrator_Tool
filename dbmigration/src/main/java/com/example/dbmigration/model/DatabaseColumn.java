// src/main/java/com/yourname/dbmigration/model/DatabaseColumn.java

package com.yourname.dbmigration.model;

import lombok.Data;

@Data
public class DatabaseColumn {
    private String name;
    private int dataType;
    private String typeName;
    private int size;
    private boolean nullable;
    private String default;
}