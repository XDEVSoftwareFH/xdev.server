
package com.xdev.server.reports;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


public abstract class EntityReportCreator extends AbstractReportCreator implements ReportCreator
{
	
	public EntityReportCreator(Object... args)
	{
		// super(args);
	}
	
	
	protected JRDataSource createDataSource(Collection<?> beanCollection,
			boolean isUseFieldDescription)
	{
		JRDataSource dataSource = new JRBeanCollectionDataSource(beanCollection,
				isUseFieldDescription);
		return dataSource;
	}
	
	
	protected JRDataSource createDataSource(Collection<?> beanCollection,
			boolean isUseFieldDescription, String... mapping)
	{
		JRDataSource dataSource = new JRBeanCollectionDataSource(beanCollection,
				isUseFieldDescription);
		
		if(mapping != null && mapping.length > 0)
		{
			Map<String, String> fieldMapping = new HashMap<String, String>();
			for(int i = 0; i < mapping.length;)
			{
				fieldMapping.put(mapping[i++],mapping[i++]);
			}
			return new JRDataSourceMapper(dataSource,fieldMapping);
		}
		
		return dataSource;
	}
	
	
	@Override
	public <T> void execute(T info)
	{
		try
		{
			ReportBuilder builder = new ReportBuilder(this.getReportStub());
			
			switch(this.getTarget())
			{
				case PREVIEW:
				{
					builder.previewReport();
				}
				break;
				
				case PRINT:
				{
					builder.print(this.getDisplayPageDialog(),this.getDisplayPageDialogOnlyOnce(),
							this.getDisplayPrintDialog(),this.getDisplayPrintDialogOnlyOnce());
				}
				break;
				
				// same behavior
				case FILE:
				case STREAM:
				{
					boolean closeStream;
					OutputStream outputStream;
					if(this.getTarget() == Target.FILE)
					{
						closeStream = true;
						outputStream = new FileOutputStream(this.getFile());
					}
					else
					{
						closeStream = false;
						outputStream = this.getOutputStream();
					}
					try
					{
						switch(this.getOutputFormat())
						{
							case PDF:
							{
								builder.writePdfFile(outputStream);
							}
							break;
							
							case HMTL:
							{
								builder.writeHtmlFile(outputStream);
							}
							break;
							
							case EXCEL:
							{
								builder.writeExcelFile(outputStream);
							}
							break;
							
							case RTF:
							{
								builder.writeRtfFile(outputStream);
							}
							break;
							
							case CSV:
							{
								builder.writeCsvFile(outputStream);
							}
							break;
							
							case XML:
							{
								builder.writeXmlFile(outputStream);
							}
							break;
							
							case TEXT:
							{
								builder.writeTextFile(outputStream);
							}
							break;
						}
					}
					finally
					{
						if(closeStream)
						{
							outputStream.close();
						}
					}
					
					// TODO case open file in new tab after creation
					// if(target == Target.FILE && openFile)
					// {
					// DesktopUtils.open(file);
					// }
				}
				break;
			}
		}
		catch(IOException e)
		{
			throw new ReportException(e);
		}
		catch(JRException e)
		{
			throw new ReportException(e);
		}
	}
	
	
	/*
	 * TODO Mit einem FileProvider arbeiten. Ein choose File Dialog oder
	 * �hnliches hat hier serverseitig nichts zu suchen.
	 */
	@Override
	public void setFile(File file)
	{
		// TODO Auto-generated method stub
		super.setFile(file);
	}
	
	
	@Override
	public File getFile()
	{
		// TODO Auto-generated method stub
		return super.getFile();
	}
	
	
	public abstract void init();
}
