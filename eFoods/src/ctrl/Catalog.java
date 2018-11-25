package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.CategoryBean;
import model.CustomerBean;
import model.Engine;
import model.ItemBean;

/**
 * Servlet implementation class Catalog
 */
@WebServlet("/Catalog.do")
public class Catalog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Regular instantiation:
		request.getSession(true);
		Engine engine = Engine.getInstance();
		HttpSession session = request.getSession();
		request.setAttribute("cart", session.getAttribute("cart"));
		request.setAttribute("sortBy", "NONE");
		// We get the categories that exist to populate the user page with options.
		try {
			List<CategoryBean> result = engine.getAllCategories();
			request.setAttribute("catalogList", result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// We check if the user is coming for a catalog option selection. Populate the
		// items only of that selected category.
		if (request.getParameter("catalogId") != null) {
			String catId = request.getParameter("catalogId");
			request.setAttribute("catalogId", catId);
			try {
				request.setAttribute("selectedCatalogName", engine.getCategory(catId).getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<ItemBean> itemList;
			try {
				itemList = engine.getCategoryItems(catId);
				request.setAttribute("itemList", itemList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			request.setAttribute("selectedCatalogName", "All items");
			try {
				List<ItemBean> itemList = engine.getAllItems();
				request.setAttribute("itemList", itemList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// If the sorted button is clicked, then get the previously selected catalog ID
		// and return the sorted category.
		if (request.getParameter("sortByButton") != null) {
			String sortBy = request.getParameter("sortBy");
			String catId = request.getParameter("catalogId");
			System.out.println(catId + ", " + sortBy);
			if (!request.getParameter("catalogId").equals("")) { // If a catalog was selected, sort the catalog items
																	// specifically.

				System.out.println(catId + ", ");
				request.setAttribute("catalogId", catId);
				request.setAttribute("sortBy", sortBy);
				try {
					request.setAttribute("selectedCatalogName", engine.getCategory(catId).getName());
					List<ItemBean> itemList = engine.getCategoryItems(catId, sortBy);
					request.setAttribute("itemList", itemList);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (request.getParameter("catalogId").equals("")) {
				System.out.println(catId + ", ");
				request.setAttribute("catalogId", catId);
				if (sortBy.matches("NONE")) {
					request.setAttribute("sortBy", "");
				}else {
					request.setAttribute("sortBy", sortBy);
				}
				try {
					request.setAttribute("selectedCatalogName", "All items");
					List<ItemBean> itemList = engine.getAllItems(sortBy);
					request.setAttribute("itemList", itemList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // If no catalog is selected, sort all the items.
				request.setAttribute("selectedCatalogName", "All items");
				request.setAttribute("sortBy", "");
				try {
					List<ItemBean> itemList = engine.getAllItems();
					request.setAttribute("itemList", itemList);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String queryString= "";
		// If the add to cart button is clicked, then we must add it to the cart:
		if (request.getParameter("cartButton") != null) {
			Map<String, Integer> cart = (Map<String, Integer>) request.getSession().getAttribute("cart");
			String item = request.getParameter("hiddenItemBeanId");
			String quantity = request.getParameter("addQuantity");
			try {
				queryString = "?catalogId=" + engine.getItem(request.getParameter("hiddenItemBeanId")).getCatId()
					+ "&sortBy=" + request.getParameter("sortBy") + "&sortByButton=1";
				Map<String, Integer> newCart = engine.addItemToCart(cart, item, quantity);
				request.getSession().setAttribute("cart", newCart);
				request.setAttribute("sortBy", request.getParameter("sortBy"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// request.setAttribute("cart", newCart);
		}
		if (session.getAttribute("customer") != null) {
			CustomerBean customer = (CustomerBean) session.getAttribute("customer");
			request.setAttribute("username", customer.getName().toString().split(" ")[0]);
		}
		
		this.getServletContext().getRequestDispatcher("/Catalog.jspx"+queryString).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
