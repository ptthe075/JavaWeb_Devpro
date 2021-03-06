<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<header class="header">
	<nav class="grid header__wrapper">
		<div class="header__content">
			<div class="header__content-left">
				<div class="header__container-nav">
					<div class="header__container-nav-icon">
						<i class="fas fa-bars"></i>
					</div>
					<div class="menu header__container-menu box-shadow">
						<ul class="menu-list">
							${menu}
							<li class="menu-item">
								<a href="#" class="menu-item__link">
									<div class="menu-item__name">
										<i class="menu-item__img" style="background: url('${base}/assets/imgs/news.svg') no-repeat center / contain;"></i>
										Tin công nghệ
									</div>
								</a>
							</li>
							<li class="menu-item">
								<a href="#" class="menu-item__link">
									<div class="menu-item__name">
										<i class="menu-item__img" style="background: url('${base}/assets/imgs/promotion.svg') no-repeat center / contain;"></i>
										Khuyến mại
									</div>
								</a>
							</li>
						</ul>
					</div>
				</div>
				<a href="${base}/" class="header__container-logo"> <img
					src="${base}/assets/imgs/logo.png" alt="Logo" width="100%">
				</a>
			</div>
			<div class="header__content-right">
				<div class="row align-items-center">
					<div class="col xl-7 lg-10 md-0 c-0 header__container">
						<div class="header__container-search border-radius">
							<input type="text" name="" id="input-search"
								placeholder="Bạn cần tìm gì?">
							<button type="button" class="header__icon-search border-radius">
								<i class="fas fa-search"></i>
							</button>
						</div>
					</div>
					<div class="col xl-5 lg-2 md-12 c-12 header__container">
						<ul class="header__container-about">
							<li class="header__container-item header__item-search"><a
								href="#" class="header__container-link" id="icon-search"> <i
									class="fas fa-search header__item-icon"></i>
							</a>
								<div class="border-radius hidden" id="search">
									<a href="#" id="close-search"> <i class="fas fa-times"></i>
									</a> <input type="text" name="" id="input-search"
										placeholder="Bạn cần tìm gì?">
									<button type="button" class="header__icon-search border-radius">
										<i class="fas fa-search"></i>
									</button>
								</div></li>
							<li
								class="header__container-item border-radius header__container-hiden">
								<a href="${base }/contact" class="header__container-link"> <i
									class="fas fa-phone-alt header__item-icon"></i> <span
									class="header__item-name">Liên hệ</span>
							</a>
							</li>
							<li class="header__container-item border-radius"><a href="${base}/cart"
								class="header__container-link cart"> <i
									class="fas fa-cart-plus header__item-icon"></i> <span
									class="header__item-name">Giỏ hàng</span>
									<span id="iconShowTotalItemsInCart">${totalItems}</span>
									</a>
							</li>
							<li class="header__container-item border-radius header__container-hiden">
								<c:choose>
									<c:when test="${isLogined && not empty userLogined}">
										<a href="${base}/account/edit-account" class="header__container-link">
											<i class="fas fa-user-circle header__item-icon"></i>
											<span class="header__item-name"><div>Xin chào,</div>${userLogined.name}</span>
										</a>
									</c:when>
									<c:otherwise>
										<a href="/login" class="header__container-link">
											<i class="fas fa-user-circle header__item-icon"></i>
											<span class="header__item-name">Đăng nhập</span>
										</a>
									</c:otherwise>
								</c:choose>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</nav>
</header>