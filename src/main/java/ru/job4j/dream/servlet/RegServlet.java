package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Store store = PsqlStore.instOf();
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        User user = new User(0, name, email, password);
        if (store.findUserByEmail(email) == null) {
            store.save(user);
            resp.sendRedirect(req.getContextPath() + "/candidates.do");
        } else {
            req.setAttribute("error", "Пользователь существует");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        }
    }
}
