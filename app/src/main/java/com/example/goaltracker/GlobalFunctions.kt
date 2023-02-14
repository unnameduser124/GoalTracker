package com.example.goaltracker

import kotlin.math.floor

fun roundDouble(value: Double, multiplier: Int): Double{
    return (floor(value * multiplier)) / multiplier
}