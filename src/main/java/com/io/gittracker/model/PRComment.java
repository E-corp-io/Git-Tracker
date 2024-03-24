package com.io.gittracker.model;

import java.io.Serializable;

public record PRComment(String username, String body) implements Serializable {}
