package com.bt.camnav.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.bt.camnav.bean.FileUpload;
import com.bt.camnav.util.DataExtractorUtil;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FileUploadController {

	public final static String INPUT_PATH = "/WEB-INF/input";
	public final static String OUTPUT_PATH = "/data";

	@Autowired
	private ServletContext context;

	@Autowired
	private CamelContext camelContext;

	@RequestMapping(value = "/ajax/api/{key}", method = RequestMethod.GET)
	public ModelAndView ajax(@PathVariable String key) {
		ModelAndView modelAndView = new ModelAndView("ajax");
		if (key != null && !key.trim().isEmpty()) {
			if (key.equals("uploadedFiles")) {
				modelAndView.addObject("response", getUploadedFilesJson()
						.toString());
			}
		}
		return modelAndView;
	}

	@RequestMapping(value = "/ajax/uploadFile/delete/{filename}.{ext}", method = RequestMethod.GET)
	public ModelAndView cmDeleteFile(@PathVariable String filename,
			@PathVariable String ext) {
		ModelAndView modelAndView = new ModelAndView("ajax");
		JsonObject json = new JsonObject();
		if (filename != null && !filename.trim().isEmpty() && ext != null
				&& !ext.trim().isEmpty()) {
			String fileNameFormat = "%s.%s";
			String fileName = String.format(fileNameFormat, filename, ext);
			boolean deleted = deleteUploadedFile(fileName);
			if (deleted) {
				// important! update the data once the file deleted
				updateData();
				json.addProperty("status", "OK");
				String messageFormat = "%s is successfully deleted.";
				json.addProperty("message",
						String.format(messageFormat, fileName));
			} else {
				json.addProperty("status", "FAIL");
				String messageFormat = "%s is failed to be deleted.";
				json.addProperty("message",
						String.format(messageFormat, fileName));
			}
			modelAndView.addObject("response", json.toString());
		} else {
			json.addProperty("status", "WARNING");
			String message = "File not found!";
			json.addProperty("message", message);
		}
		return modelAndView;
	}

	@RequestMapping(value = "/ajax/uploadFile", method = RequestMethod.POST)
	public ModelAndView cmFileUpload(@RequestParam("file") MultipartFile file) {		
		ModelAndView modelAndView = new ModelAndView("ajax");
		boolean success = processUploadedFile(file);
		
		String fileName = "The file";
		String message = "";

		if (file != null) {
			fileName = file.getOriginalFilename();
		}

		JsonObject json = new JsonObject();

		if (success) {
			json.addProperty("status", "OK");
			String messageFormat = "%s is successfully uploaded.";
			message = String.format(messageFormat, fileName);
		} else {
			json.addProperty("status", "FAIL");
			String messageFormat = "%s is failed to be uploaded.";
			message = String.format(messageFormat, fileName);
		}

		json.addProperty("message", message);
		modelAndView.addObject("response", json.toString());
		return modelAndView;
	}

	@RequestMapping(value = "/fileUpload", method = RequestMethod.GET)
	public ModelAndView uploadForm() {
		ModelAndView modelAndView = new ModelAndView("fileUpload");
		modelAndView.addObject("fileUpload", new FileUpload());
		return modelAndView;
	}

	@RequestMapping(value = "/fileUpload/delete/{fileName}.{ext}", method = RequestMethod.GET)
	public ModelAndView deleteUploadedFile(@PathVariable String fileName,
			@PathVariable String ext) {

		StringBuilder message = new StringBuilder(fileName);
		message = message.append('.').append(ext);

		ModelAndView modelAndView = new ModelAndView("fileUpload");

		boolean deleted = deleteUploadedFile(message.toString());

		if (deleted) {
			updateData();
			message = message.append(" is successfully deleted.");
		} else {
			message = message.append(" is not able to be deleted.");
		}
		modelAndView.addObject("message", message.toString());
		modelAndView.addObject("uploadedFiles", getUploadedFiles());

		return modelAndView;
	}

	// TODO: download the file
	@RequestMapping(value = "/fileUpload/download/{fileName}.{ext}", method = RequestMethod.GET)
	public ModelAndView downloadFile(@PathVariable String fileName,
			@PathVariable String ext, HttpServletResponse response) {

		StringBuilder message = new StringBuilder(fileName);
		message = message.append('.').append(ext);

		ModelAndView modelAndView = new ModelAndView("fileUpload");

		List<File> files = getUploadedFiles();
		for (File file : files) {
			if (file.getName().equals(message.toString())) {

				// TODO: pushing the file
				try {
					InputStream is = new FileInputStream(file);
					IOUtils.copy(is, response.getOutputStream());
					response.flushBuffer();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		message = message.append(" is ready to be downloaded.");
		modelAndView.addObject("message", message.toString());
		modelAndView.addObject("uploadedFiles", getUploadedFiles());

		return modelAndView;
	}

	private File getUploadDir() {
		String directoryPath = context.getRealPath(INPUT_PATH);
		File dir = new File(directoryPath);
		return dir;
	}

	private List<File> getUploadedFiles() {
		File dir = getUploadDir();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			List<File> list = new ArrayList<File>(files.length);
			for (File file : files) {
				list.add(file);
			}
			return list;
		}
		return null;
	}

	private boolean deleteUploadedFile(String filename) {
		boolean deleted = false;
		List<File> files = getUploadedFiles();
		for (File file : files) {
			if (file.getName().equals(filename)) {
				deleted = file.delete();
			}
		}
		return deleted;
	}

	private boolean processUploadedFile(MultipartFile multipartFile) {
		if (multipartFile != null) {
			InputStream is;
			try {
				is = multipartFile.getInputStream();

				ProducerTemplate template = camelContext.createProducerTemplate();
				template.sendBody("direct:processCsv", is);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void updateData() {
		String directoryPath = context.getRealPath(INPUT_PATH);
		String outputDirPath = context.getRealPath(OUTPUT_PATH);
		File outputDir = new File(outputDirPath);

		if (!outputDir.exists()) {
			outputDir.mkdirs();
		} else {
			// TODO: empty output dir properly
			boolean deleted = true;
			for (File file : outputDir.listFiles()) {
				if (!file.delete()) {
					deleted = false;
				}
			}
			if (!deleted) {
				System.err.println("Not able to empty folder "
						+ outputDir.getAbsolutePath());
			}
		}

		DataExtractorUtil.extractFiles(directoryPath, outputDir);
	}

	private JsonArray getUploadedFilesJson() {
		JsonArray arr = new JsonArray();
		List<File> files = getUploadedFiles();
		if (files != null) {
			for (File file : files) {
				JsonObject json = new JsonObject();
				json.addProperty("name", file.getName());
				arr.add(json);
			}
		}
		return arr;
	}
}
