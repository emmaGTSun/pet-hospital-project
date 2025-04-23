package com.example.controller;

import com.example.common.Result;
import com.example.entity.Article;
import com.example.entity.Params;
import com.example.service.ArticleService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@CrossOrigin // Solving Cross-Domain Issues
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Article> info= articleService.findBySearch(params);
        return Result.success(info);
    }

    @PostMapping
    public Result save(@RequestBody Article article) {
        if (article.getId() == null) {
            articleService.add(article);
        } else {
            articleService.update(article);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        articleService.delete(id);
        return Result.success();
    }


}
