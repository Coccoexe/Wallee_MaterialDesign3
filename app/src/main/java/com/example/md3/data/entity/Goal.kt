package com.example.md3.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    @ColumnInfo
    val userMail: String,
    @ColumnInfo
    val category: String,
    @ColumnInfo
    val sum: Double,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val completed : Boolean,
    @ColumnInfo
    val toNotify : Boolean
)
{
    var isSelected = false

    //completed controlla se il goal e' completato
    //viene aggiornato solo nell addTransaction


    //controllo se devo ancora notificare l'utente del goal completato
    //viene aggiornato nel goalFragment

}