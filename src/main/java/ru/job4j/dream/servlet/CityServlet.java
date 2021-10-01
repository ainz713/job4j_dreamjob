package ru.job4j.dream.servlet;

import com.google.gson.Gson;
import ru.job4j.dream.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CityServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String answer = gson.toJson(PsqlStore.instOf().findAllCities());
        PrintWriter writer = new PrintWriter(resp.getOutputStream(), true, StandardCharsets.UTF_8);
        writer.println(answer);
        writer.flush();
    }
}
