package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class CandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", Store.instOf().findAllCandidates());
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if ("delete".equals(req.getParameter("method"))) {
            Store.instOf().removeCandidate(Integer.parseInt(req.getParameter("id")));
            File file = new File("c:\\images\\" + req.getParameter("id"));
            file.delete();
        } else {
            Store.instOf().save(
                    new Candidate(
                            Integer.parseInt(req.getParameter("id")),
                            req.getParameter("name")
                    )
            );
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
