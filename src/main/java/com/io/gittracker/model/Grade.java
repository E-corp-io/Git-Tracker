package com.io.gittracker.model;

import java.io.Serializable;

public record Grade(double grade, String message) implements Serializable {}
