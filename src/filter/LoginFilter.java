package filter;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.App;
import model.User;
import jdbc.AppJDBC;
import jdbc.UsrJDBC;
import constants.UrlConstant;

public class LoginFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub

		HttpServletRequest req = (HttpServletRequest) arg0;  
		HttpServletResponse resp = (HttpServletResponse) arg1;   
		HttpSession session = req.getSession();   
		User user = (User) session.getAttribute("user");
		String token = req.getParameter("token");
		// 设置session
		if(user==null){
			try {
				user = UsrJDBC.findUserByToken(token);
				if(user==null){
					resp.sendRedirect(UrlConstant.login);
				}else{
					session.setAttribute("user", user);
					arg2.doFilter(arg0, arg1);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		else {
			arg2.doFilter(arg0, arg1);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
