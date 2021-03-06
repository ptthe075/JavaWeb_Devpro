package com.devpro.shop16.ppthe.controller;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devpro.shop16.ppthe.entities.Brand;
import com.devpro.shop16.ppthe.entities.Category;
import com.devpro.shop16.ppthe.entities.Information;
import com.devpro.shop16.ppthe.entities.Product;
import com.devpro.shop16.ppthe.entities.Role;
import com.devpro.shop16.ppthe.entities.Slider;
import com.devpro.shop16.ppthe.entities.User;
import com.devpro.shop16.ppthe.services.BrandService;
import com.devpro.shop16.ppthe.services.CategoryService;
import com.devpro.shop16.ppthe.services.InformationService;
import com.devpro.shop16.ppthe.services.ProductService;
import com.devpro.shop16.ppthe.services.SliderService;

public abstract class BaseController {
	@Autowired
	protected ProductService productService;
	
	@Autowired
	protected CategoryService categoryService;

	@Autowired
	protected BrandService brandService;

	@Autowired
	protected SliderService sliderService;
	
	@Autowired
	protected InformationService informationService;
	
	@Autowired
    protected JavaMailSender emailSender;
	
	public void sentEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
        
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        this.emailSender.send(message);
	}
	

	@ModelAttribute("categories")
	public List<Category> getAllCategories() {
		return categoryService.findAll();
	}

	@ModelAttribute("brands")
	public List<Brand> getAllBrands() {
		return brandService.findAll();
	}

	@ModelAttribute("sliders")
	public List<Slider> getAllSliders() {
		return sliderService.findAll();
	}

	@ModelAttribute("isLogined")
	public boolean isLogined() {
		boolean isLogined = false;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			isLogined = true;
		}
		return isLogined;
	}
	
	@ModelAttribute("userLogined")
	public User getUserLogined() {
		Object userLogined = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userLogined != null && userLogined instanceof UserDetails)
			return (User) userLogined;

		return null;
	}
	
	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		User userAdmin = getUserLogined();
		
		if(userAdmin != null) {
			for (Role r : userAdmin.getRoles()) {
				if(r.getAuthority().equals("ADMIN")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@ModelAttribute("categoryParents")
	public List<Category> categoryParents(){
		String sql = "SELECT * FROM tbl_category WHERE parent_id IS NULL AND status = 1";
		return categoryService.executeByNativeSQL(sql, 0).getData();
	}
	
	@ModelAttribute("infomations")
	public List<Information> infomations(){
		return informationService.findAll();
	}
	
	private Map<Integer, Set<Brand>> brandGroupByCategory;

	@ModelAttribute("menu")
	public String buildMenu() {
		List<Product> products = productService.findAll();
		Map<Integer, List<Product>> productGroupByCategory = products.stream().collect(Collectors.groupingBy(Product::getCategoryId));
		brandGroupByCategory = new LinkedHashMap<Integer, Set<Brand>>();
		
		for (Integer categoryId : productGroupByCategory.keySet()) {
			Set<Brand> brands = new LinkedHashSet<Brand>();
			for (Product p : productGroupByCategory.get(categoryId)) {
				brands.add(p.getBrand());
			}
			
			brandGroupByCategory.put(categoryId, brands);
		}
		
		StringBuilder menu = new StringBuilder();

		List<Category> categoryParents = categoryParents();

		for (Category c : categoryParents) {
			menu.append(
					"<li class=\"menu-item\"><a href=\"/category/"+c.getSeo()+"\" class=\"menu-item__link\"> <div class=\"menu-item__name\">");
			
			if (c.getImage() != null && !c.getImage().isEmpty()) {
				menu.append("<i class=\"menu-item__img\" style=\"background: url('/Upload/Categories/" + c.getImage() + "') no-repeat center / contain;\"></i>");
			}
			menu.append(c.getName() + "</div>");
			
			Set<Brand> listBrand = brandGroupByCategory.get(c.getId());
			if (c.getChilds() != null && !c.getChilds().isEmpty() || listBrand != null && !listBrand.isEmpty()) {
				menu.append("<i class=\"fas fa-angle-right menu-item__icon\"></i></a>");
				menu.append("<ul class=\"menu-list__sub box-shadow\">");
				if(listBrand != null && !listBrand.isEmpty()) {
					buildBrand(menu, listBrand, c.getSeo());
				}
				if(c.getChilds() != null && !c.getChilds().isEmpty()) {
					buildSubMenu(menu, c.getChilds());					
				}

				menu.append("</ul>");
			} else {
				menu.append("</a>");
			}
			menu.append("</li>");
		}

		return menu.toString();
	}

	private void buildBrand(StringBuilder menu, Set<Brand> listBrand, String categorySeo) {
		for (Brand b : listBrand) {
			menu.append("<li class=\"menu-item\"><a href=\"/search/"+categorySeo+"/"+b.getSeo()+"\" class=\"menu-item__link\"> <div class=\"menu-item__name\">"+b.getName() + "</div></a></li>");
		}
	}

	private void buildSubMenu(StringBuilder menu, Set<Category> childs) {
		for (Category c : childs) {
			menu.append("<li class=\"menu-item\"><a href=\"/category/"+c.getSeo()+"\" class=\"menu-item__link\"> <div class=\"menu-item__name\">");

			if (c.getImage() != null && !c.getImage().isEmpty()) {
				menu.append("<i class=\"menu-item__img\" style=\"background: url('/Upload/Categories/" + c.getImage()+ "') no-repeat center / contain;\"></i>");
			}
			menu.append(c.getName() + "</div>");

			Set<Brand> listBrand = brandGroupByCategory.get(c.getId());
			if (c.getChilds() != null && !c.getChilds().isEmpty() || listBrand != null && !listBrand.isEmpty()) {
				menu.append("<i class=\"fas fa-angle-right menu-item__icon\"></i></a>");
				menu.append("<ul class=\"menu-list__sub box-shadow\">");
				if(listBrand != null && !listBrand.isEmpty()) {
					buildBrand(menu, listBrand, c.getSeo());
				}
				if(c.getChilds() != null && !c.getChilds().isEmpty()) {
					buildSubMenu(menu, c.getChilds());					
				}

				menu.append("</ul>");
			} else {
				menu.append("</a>");
			}
			menu.append("</li>");
		}
		
		
	}

	public int getCurrentPage(HttpServletRequest request) {
		try {
			return Integer.parseInt(request.getParameter("page"));
		} catch (Exception e) {
			return 1;
		}
	}

	public Integer getInteger(HttpServletRequest request, String paramName) {
		try {
			return Integer.parseInt(request.getParameter(paramName));
		} catch (Exception e) {
			return null;
		}
	}
}
